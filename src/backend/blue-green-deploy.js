/**
 * Script to automate blue green deployment
 * node blue-green-deploy.js dockerImageName defaultPort dockerFilePath networkInterface SSHUserName
 */

const { execSync } = require('child_process');

const NETWORK_INTERFACE = process.argv.slice(2)[3];
const SSH_USER = process.argv.slice(2)[4];

const main = async () => {
    const latestCommit = execSync("git log -n 1 --pretty=format:\"%H\"").toString().substring(0, 10);
    const args = process.argv.slice(2);
    const oldNodes = await getNodes();
    await provisionGreenDeployments(oldNodes, args[0], Number(args[1]), latestCommit, args[2]);
    await decomissionBlueDeployments(oldNodes, latestCommit);
}

const sleep = (timeout) => new Promise(resolve => setTimeout(resolve, timeout));

const fetchWithErrorHandling = async (url, body=undefined) => {
    const response = await (body === undefined ? fetch(url) : fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body),
    }));
    if (!response.ok) {
        throw new Error(`Fetching ${url} failed with ${await response.text()}`);
    }
    return await response.json();
}

const execSSH = (host, command) => {
    return execSync(`ssh ${SSH_USER}@${host} -t "${command}"`, { stdio: 'ignore' });
}

const getNodes = async () => {
    const res = await fetchWithErrorHandling("http://127.0.0.1:2019/config/apps/http/servers/srv0/routes/0/handle/0/routes/0/handle/0/upstreams");
    return res.map((it) => it['dial']);
}

const startNewInstance = (host, baseImageName, basePort, newVersion, dockerPath) => {
    const [hostName, port] = host.split(":");
    const newPort = Number(port) === basePort ? basePort + 1 : basePort;
    const newImageName = baseImageName + '-' + newVersion;
    const localIp = execSync(`ifconfig ${NETWORK_INTERFACE} | grep -Eo 'inet (addr:)?([0-9]*\\.){3}[0-9]*' | grep -Eo '([0-9]*\\.){3}[0-9]*' | grep -v '127.0.0.1'`).toString().split("\n")[0];
    execSSH(hostName, `cd Expense-Tracker-v2 && git pull && cd ${dockerPath} && docker build -t ${newImageName} --build-arg host=${localIp}" . && docker run -dp ${localIp}:${newPort}:${basePort} ${newImageName}`);
    return `${hostName}:${newPort}`;
}

const provisionGreenDeployments = async (nodes, baseImageName, basePort, newVersion, dockerPath) => {
    const newNodes = nodes.map((it) => startNewInstance(it, baseImageName, basePort, newVersion, dockerPath)).map((it) => ({ dial: it }));
    // Wait for all new nodes to spin up
    await sleep(10000);
    await Promise.all(newNodes.map(async (it) => await fetchWithErrorHandling(`http://${it.dial}/healthcheck`)));
    await fetchWithErrorHandling("http://127.0.0.1:2019/config/apps/http/servers/srv0/routes/0/handle/0/routes/0/handle/0/", { upstreams: newNodes });
    return newNodes;
}

const decomissionBlueDeployments = async (oldNodes, newVersion) => {
    for (let node of oldNodes) {
        const [hostName, _] = node.split(":");
        await execSSH(hostName, `docker ps -a --format '{{.Names}}' | grep -v "${newVersion}" | xargs docker kill && docker ps -a --format '{{.Names}}' | grep -v "${newVersion}" | xargs docker rmi`)
    }
};

main();

