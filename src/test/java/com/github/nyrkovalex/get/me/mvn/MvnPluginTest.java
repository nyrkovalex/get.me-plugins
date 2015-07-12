package com.github.nyrkovalex.get.me.mvn;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.seed.test.MockedTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MvnPluginTest extends MockedTest {

	@Mock Mvn mvn;
	@Mock Mvn.Runner runner;
	@Mock Path workingDir;
	@Mock GetMe.ExecutionContext context;
	@InjectMocks MvnBuilder builder;

	@Before
	public void setUp() throws Exception {
		when(context.getCwd()).thenReturn(workingDir);
		when(runner.enableOutput(anyBoolean())).thenReturn(runner);
		when(mvn.run(anyObject())).thenReturn(runner);
	}

	@Test
	public void testShouldUseDefaultTargets() throws Exception {
		builder.exec(context, Optional.of(new MvnParams()));
		verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
	}

	@Test
	public void testShouldUseProvidedTargets() throws Exception {
		List<String> targets = Arrays.asList("bar", "baz");
		builder.exec(context, Optional.of(new MvnParams(targets)));
		verify(mvn).run(targets);
	}

	@Test
	public void testShouldUseDefaultTargetsOnNullParams() throws Exception {
		Optional<MvnParams> params = Optional.empty();
		builder.exec(context, params);
		verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
	}

	@Test
	public void testShouldUseDefaultTargetsOnNullGoals() throws Exception {
		builder.exec(context, Optional.of(new MvnParams()));
		verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
	}

	@Test
	public void testShouldEnableMvnOutputInDebugMode() throws Exception {
		when(context.isDebug()).thenReturn(true);
		builder.exec(context, Optional.of(new MvnParams()));
		verify(runner).enableOutput(true);
	}

	@Test
	public void testShouldRunInWorkingDir() throws Exception {
		builder.exec(context, Optional.of(new MvnParams()));
		verify(runner).in(workingDir);
	}
}
