package com.github.nyrkovalex.get.me.install;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.get.me.env.Envs;
import com.gtihub.nyrkovalex.seed.nio.Fs;

public class ExecJarInstaller implements GetMe.Plugin<JarParams> {

	private final Fs fs;
	private final Envs.Env env;

	public ExecJarInstaller() {
		this.fs = Fs.instance();
		this.env = Envs.env();
	}

	ExecJarInstaller(Fs fs, Envs.Env env) {
		this.fs = fs;
		this.env = env;
	}

	@Override
	public void exec(Path workingDir, Optional<JarParams> params) throws GetMe.Err {
		JarParams jarParams = params.orElseThrow(() -> {
			return new GetMe.Err("`jar` parameter must be provided");
		});
		String targetPath = jarParams.jar;
		try {
			Path sourceFile = sourceJar(workingDir, targetPath);
			Path targetFile = targetFile(sourceFile.getFileName());
			fs.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException err) {
			throw new GetMe.Err(String.format("Failed to copy %s to %s", targetPath, env.jarPath()), err);
		}
	}

	private Path targetFile(Path sourceFileName) throws GetMe.Err {
		String jarPath = env.jarPath();
		if (Objects.isNull(jarPath) || jarPath.isEmpty()) {
			throw new GetMe.Err("JARPATH environment variable is not set");
		}
		return fs.path(jarPath).resolve(sourceFileName);
	}

	private Path sourceJar(Path workingDir, String targetPath) throws GetMe.Err {
		if (Objects.isNull(targetPath) || targetPath.isEmpty()) {
			throw new GetMe.Err("No \"jar\" param provided");
		}
		Path sourceFile = workingDir.resolve(targetPath);
		if (!fs.exists(sourceFile)) {
			throw new GetMe.Err(sourceFile + " does not exist");
		}
		return sourceFile;
	}

	@Override
	public Optional<Class<JarParams>> paramsClass() {
		return Optional.of(JarParams.class);
	}

}
