import {globby} from "globby";
import babel from "@babel/core";
import fs from "node:fs/promises";
import path from "node:path";

export default class Transpiler {
	#sourceDir;
	#targetDir;
	#excludePatterns;

	constructor(sourceDir, targetDir, excludePatterns) {
		this.#sourceDir = sourceDir;
		this.#targetDir = targetDir;
		this.#excludePatterns = excludePatterns;
	}

	// Transpile all JavaScript files from sourceDir to targetDir
	// Copy all non-JavaScript files without modifications
	async transpileAll() {
		console.log(`Transpiling from ${this.#sourceDir} to ${this.#targetDir}`);
		const globOptions = {
			cwd: this.#sourceDir,
			followSymbolicLinks: false,
			dot: true,
			absolute: true
		};
		if (this.#excludePatterns?.length) {
			globOptions.ignore = this.#excludePatterns;
		}
		const filePaths = await globby("**/*", globOptions);
		let jsFilesCounter = 0;
		let otherFilesCounter = 0;
		await Promise.all(filePaths.map(async (filePath) => {
			if (filePath.endsWith(".js")) {
				jsFilesCounter++;
				await this.transpileFile(filePath);
			} else {
				otherFilesCounter++;
				await this.copyFile(filePath);
			}
		}));
		console.log(`Finished transpiling to ${this.#targetDir}:`);
		console.log(`  Transpiled ${jsFilesCounter} JavaScript resources. ` +
			`Copied ${otherFilesCounter} other resources without modifications`);
	}

	async transpileFile(filePath) {
		// Transpile file content
		const content = await babel.transformFileAsync(filePath, {
			configFile: false,
			babelrc: false,
			filename: filePath,
			presets: [
				["@babel/preset-env", {
					targets: {
						rhino: "1.7.4"
					}
				}]
			]
		});

		// Write to target, preserving the directory structure
		const targetFilePath = filePath.replace(this.#sourceDir, this.#targetDir);
		await fs.mkdir(path.dirname(targetFilePath), {recursive: true});
		await fs.writeFile(targetFilePath, content.code);
	}

	async copyFile(filePath) {
		const targetFilePath = filePath.replace(this.#sourceDir, this.#targetDir);
		await fs.mkdir(path.dirname(targetFilePath), {recursive: true});
		await fs.copyFile(filePath, targetFilePath);
	}

	static async transpileProject(targetDir, sourceDir, testDir) {
		const resolvedSrc = path.resolve(sourceDir);
		const resolvedTest = path.resolve(testDir);

		const srcExcludes = [];
		// Check if testDir is located within sourceDir
		// In that case, split them into separate src and test dirs
		if (resolvedTest.startsWith(resolvedSrc)) {
			const relativeTest = path.relative(resolvedSrc, resolvedTest);
			srcExcludes.push(`${relativeTest}/**`);
		}
		const srcTranspiler = new Transpiler(sourceDir, path.join(targetDir, "src"), srcExcludes);

		let testTranspiler;
		if (testDir) {
			testTranspiler = new Transpiler(testDir, path.join(targetDir, "test"));
		}

		await Promise.all([
			srcTranspiler.transpileAll(),
			testTranspiler && testTranspiler.transpileAll()
		]);
	}
}
