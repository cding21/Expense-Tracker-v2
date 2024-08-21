/**
 * Script to automate blue green deployment
 * node blue-green-deploy.js dockerImageName defaultPort dockerFilePath
 */

const { execSync } = require('child_process');

const main = async () => {
    const latestCommit = execSync("git log -n 1 --pretty=format:\"%H\"").toString().substring(0, 10);
    const args = process.argv.slice(1);
    const oldNodes = await getNodes();
    const newNodes = await provisionGreenDeployments(oldNodes, args[0], Number(args[1]), latestCommit, args[2]);
    // Wait 30 seconds
    await sleep(30000);
    await decomissionBlueDeployments(oldNodes, newNodes, latestCommit);
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

const execSSH = async (host, command) => {
    return await execSync(`ssh user@${host} -t "${command}"`);
}

const getNodes = async () => {
    const res = await fetchWithErrorHandling("http://127.0.0.1:2019/config/apps/http/servers/srv0/routes/0/handle/0/routes/0/handle/0/upstreams");
    return res.map((it) => it['dial']);
}

const startNewInstance = async (host, baseImageName, basePort, newVersion, dockerPath) => {
    const [hostName, port] = host.split(":");
    const newPort = Number(port) === basePort ? basePort + 1 : basePort;
    const newImageName = baseImageName + '-' + newVersion;
    await execSSH(hostName, `cd Expense-Tracker-v2 && git pull && cd ${dockerPath} && docker build -t ${newImageName} . && docker run -dp 127.0.0.1:${newPort}:${basePort} ${newImageName}`);
    return `${hostName}:${newPort}`;
}

const provisionGreenDeployments = async (nodes, baseImageName, basePort, newVersion, dockerPath) => {
    const newNodes = (await Promise.all(nodes.map(async (it) => await startNewInstance(it, baseImageName, basePort, newVersion, dockerPath)))).map((it) => ({ dial: it }));
    await fetchWithErrorHandling("http://127.0.0.1:2019/config/apps/http/servers/srv0/routes/0/handle/0/routes/0/handle/0/", { upstreams: [...nodes, ...newNodes] });
    return newNodes;
}

const decomissionBlueDeployments = async (oldNodes, newNodes, newVersion) => {
    for (let node of oldNodes) {
        const [hostName, _] = node.split(":");
        await execSSH(hostName, `docker ps -a --format '{{.Names}}' | grep -v "${newVersion}" | xargs docker kill && docker ps -a --format '{{.Names}}' | grep -v "${newVersion}" | xargs docker rmi`)
    }
    await fetchWithErrorHandling("http://127.0.0.1:2019/config/apps/http/servers/srv0/routes/0/handle/0/routes/0/handle/0/", { upstreams: newNodes });
};

main();

