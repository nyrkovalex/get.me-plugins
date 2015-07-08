package com.github.nyrkovalex.get.me.install;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class ExecJarInstallerTest {

	@Mock Fs fs;
	@Mock Envs.Env env;
	@Mock Path sourceJar;
	@Mock Path targetFile;
	@Mock Path jarFileName;
	@Mock Path jarPath;
	@Mock Path workingDir;
	@InjectMocks ExecJarInstaller installer;

	Optional<JarParams> params;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(fs.exists(sourceJar)).thenReturn(true);
		when(sourceJar.getFileName()).thenReturn(jarFileName);
		when(env.jarPath()).thenReturn("jarPath");
		when(fs.path("jarPath")).thenReturn(jarPath);
		when(workingDir.resolve("myJar")).thenReturn(sourceJar);
		when(jarPath.resolve(jarFileName)).thenReturn(targetFile);

		params = Optional.of(new JarParams("myJar"));
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowWhenNoJarParamFound() throws Exception {
		installer.exec(workingDir, Optional.of(new JarParams()));
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowOnEmptyJarParam() throws Exception {
		installer.exec(workingDir, Optional.of(new JarParams("")));
	}

	@Test
	public void testShouldCopyExecutableJarToJarPath() throws Exception {
		installer.exec(workingDir, params);
		verify(fs).copy(sourceJar, targetFile, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowIfNoJarExists() throws Exception {
		when(fs.exists(sourceJar)).thenReturn(false);
		installer.exec(workingDir, params);
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowIfJarPathIsNotSet() throws Exception {
		when(env.jarPath()).thenReturn(null);
		installer.exec(workingDir, params);
	}

}
