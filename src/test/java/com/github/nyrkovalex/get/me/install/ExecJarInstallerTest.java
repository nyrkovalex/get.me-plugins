package com.github.nyrkovalex.get.me.install;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.get.me.env.Envs;
import com.github.nyrkovalex.seed.Io;

public class ExecJarInstallerTest {

	@Mock Io.Fs fs;
	@Mock Envs.Env env;
	@Mock Io.File sourceJar;
	@Mock Io.File targetFile;
	final String workingDir = "/tmp";
	@InjectMocks ExecJarInstaller installer;

	Optional<JarParams> params;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(fs.file("/tmp", "myJar")).thenReturn(sourceJar);
		when(sourceJar.exists()).thenReturn(Boolean.TRUE);
		when(sourceJar.name()).thenReturn("myJar");
		when(env.jarPath()).thenReturn("jarPath");
		when(fs.file("jarPath", "myJar")).thenReturn(targetFile);

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
		verify(sourceJar).copyTo(targetFile);
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowIfNoJarExists() throws Exception {
		when(sourceJar.exists()).thenReturn(Boolean.FALSE);
		installer.exec(workingDir, params);
	}

	@Test(expected = GetMe.Err.class)
	public void testShouldThrowIfJarPathIsNotSet() throws Exception {
		when(env.jarPath()).thenReturn(null);
		installer.exec(workingDir, params);
	}

}
