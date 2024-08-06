const { spawn, execSync } = require('child_process');

const main = async () => {
    const args = process.argv.slice(2);
    if (args[0] && args[0].includes("initial")) {
        await initMongoDB(true)
        await initRedisDB(true)
    } else {
        await initMongoDB(false)
        await initRedisDB(false)
    }
}

const argumentParser = (command) => {
    let prevIndex = 0;
    let executable = ""
    let args = [];
    let isInQuote = false;

    for (let i = 0; i < command.length; i++) {
        if (command[i] === '"' || command[i] === "'") {
            if (isInQuote) {
                isInQuote = false
            } else {
                isInQuote = true
            }
        }

        if (command[i] === ' ' && !isInQuote) {
            if (prevIndex === 0) {
                executable = command.slice(prevIndex, i)
            } else {
                const segment = command.slice(prevIndex, i);
                args.push(segment)
            }
            prevIndex = i + 1
        }
    }

    args.push(command.slice(prevIndex, command.length))
    return args
}

const spawnSync = (command) => {
    return new Promise((resolve, reject) => {
        const proc = spawn(command.split(" ")[0], argumentParser(command), { shell: true })
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

const initRedisDB = async (isInitial) => {
    const res = execSync("docker container ls");
    if (res.includes("redis")) {
        console.log('Redis is already running')
        return;
    }

    if (isInitial) {
        await spawnSync("redis/redis-stack-server:latest")
    }
    await spawnSync('docker run --rm -d --name redis -p 6379:6379 -e REDIS_ARGS="--requirepass defaultpassword" redis/redis-stack-server:latest')
}

main();