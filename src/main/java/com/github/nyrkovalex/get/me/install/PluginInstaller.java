package com.github.nyrkovalex.get.me.install;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.get.me.env.Envs;
import com.gtihub.nyrkovalex.seed.nio.Fs;

public class PluginInstaller implements GetMe.Plugin<JarParams> {

	private final Envs.Env env;
	private final Fs fs;

	public PluginInstaller() {
		this(Envs.env(), Fs.instance());
	}

	PluginInstaller(Envs.Env env, Fs fs) {
		this.env = env;
		this.fs = fs;
	}

	@Override
	public void exec(Path workingDir, Optional<JarParams> params) throws GetMe.Err {
		JarParams jarParams = params.orElseThrow(() -> {
			return new GetMe.Err("`jar` parameter must be provided");
		});
		try {
			Path pluginsDir = fs.path(env.pluginsHome());
			Path targetJar = workingDir.resolve(jarParams.jar);
			fs.copy(targetJar, pluginsDir, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException err) {
			throw new GetMe.Err(
					String.format("Failed to copy %s to %s", jarParams.jar, env.pluginsHome()),
					err
			);
		}
	}

	@Override
	public Optional<Class<JarParams>> paramsClass() {
		return Optional.of(JarParams.class);
	}
}
