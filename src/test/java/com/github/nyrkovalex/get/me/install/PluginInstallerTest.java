package com.github.nyrkovalex.get.me.install;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.get.me.env.Envs;
import com.gtihub.nyrkovalex.seed.nio.Fs;


public class PluginInstallerTest {
	
	@Mock private Path jarFile;
	@Mock private Fs fs;
	@Mock private Path pluginsDir;
	@Mock private Path workingDir;
	@Mock private Envs.Env env;

	@InjectMocks private PluginInstaller installer;

	private final JarParams params = new JarParams("plugin.jar");
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(workingDir.resolve("plugin.jar")).thenReturn(jarFile);
		when(env.pluginsHome()).thenReturn("/home/me/get.me/plugins");
		when(fs.path("/home/me/get.me/plugins")).thenReturn(pluginsDir);
	}
	
	
	@Test(expected = GetMe.Err.class)
	public void testShouldThrowWhenNoJarParamsPresent() throws Exception {
		installer.exec(workingDir, Optional.empty());
	}
	
	@Test
	public void testShouldCopyJarToPluginsDir() throws Exception {
		installer.exec(workingDir, Optional.of(params));
		verify(fs).copy(jarFile, pluginsDir, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test(expected = GetMe.Err.class)
	public void testShouldRethrowIoError() throws Exception {
		when(fs.copy(jarFile, pluginsDir, StandardCopyOption.REPLACE_EXISTING)).thenThrow(new IOException());
		installer.exec(workingDir, Optional.of(params));
	}
}