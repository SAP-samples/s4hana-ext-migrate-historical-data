import Transpiler from "./Transpiler.js";

try {
	const targetDir = process.argv[2];
	const sourceDir = process.argv[3];
	const testDir = process.argv[4];
	if (!targetDir || !sourceDir || !testDir) {
		throw new Error(
			"Missing parameters. Usage: transpile.js <targetDir> <sourceDir> <testDir>");
	}
	await Transpiler.transpileProject(targetDir, sourceDir, testDir);
} catch (err) {
	console.error(err);
	process.exit(1);
}

