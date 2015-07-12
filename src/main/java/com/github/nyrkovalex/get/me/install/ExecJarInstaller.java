package com.github.nyrkovalex.get.me.install;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.seed.Sys;
import com.gtihub.nyrkovalex.seed.nio.Fs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class ExecJarInstaller implements GetMe.Plugin<JarParams> {

	private final Fs fs;
	private final Sys.Environment env;

	public ExecJarInstaller() {
		this(Fs.instance(), Sys.environment());
	}

	ExecJarInstaller(Fs fs, Sys.Environment env) {
		this.fs = fs;
		this.env = env;
	}

	@Override
	public void exec(GetMe.ExecutionContext context, Optional<JarParams> params) throws GetMe.PluginException {
		JarParams jarParams = params.orElseThrow(
				() -> new GetMe.PluginException("`jar` parameter must be provided"));
		String targetPath = jarParams.jar;
		String jarPath = readJarPath();
		try {
			Path sourceFile = sourceJar(context.getCwd(), targetPath);
			Path targetFile = fs.path(jarPath).resolve(sourceFile.getFileName());
			fs.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException err) {
			throw new GetMe.PluginException(String.format("Failed to copy %s to %s", targetPath, jarPath), err);
		}
	}

	private String readJarPath() throws GetMe.PluginException {
		return env.readVar("JARPATH").orElseThrow(
				() -> new GetMe.PluginException("JARPATH environment variable is not set"));
	}

	private Path sourceJar(Path workingDir, String targetPath) throws GetMe.PluginException {
		if (Objects.isNull(targetPath) || targetPath.isEmpty()) {
			throw new GetMe.PluginException("No \"jar\" param provided");
		}
		Path sourceFile = workingDir.resolve(targetPath);
		if (!fs.exists(sourceFile)) {
			throw new GetMe.PluginException(sourceFile + " does not exist");
		}
		return sourceFile;
	}

	@Override
	public Optional<Class<JarParams>> paramsClass() {
		return Optional.of(JarParams.class);
	}

}
