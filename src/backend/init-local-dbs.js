const { spawn, execSync } = require('child_process');

const main = async () => {
    const args = process.argv.slice(2);
    if (args[0] && args[0].includes("initial")) {
        initMongoDB(true)
    } else {
        initMongoDB(false)
    }
}

const spawnSync = (command) => {
    const args = command.split(" ")

    return new Promise((resolve, reject) => {
        const proc = spawn(args[0], args.slice(1))
        proc.stdout.on('data', (data) => {
            console.log(data.toString())
        })
        proc.stderr.on('data', (data) => {
            console.log(data.toString())
        })
        proc.on('exit', () => {
            resolve()
        })
    })
}

const initMongoDB = async (isInitial) => {
    const res = execSync("docker container ls");
    if (res.includes("mongodb")) {
        console.log('MongoDB is already running')
        return;
    }

    if (isInitial) {
        await spawnSync("docker pull mongodb/mongodb-community-server:latest")
    }
    await spawnSync("docker run --rm --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=password -d mongodb/mongodb-community-server:latest")
}

main();