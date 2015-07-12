package com.github.nyrkovalex.get.me.mvn;

import com.github.nyrkovalex.get.me.api.GetMe;

import java.util.List;
import java.util.Optional;

abstract class MvnPlugin implements GetMe.Plugin<MvnParams> {

	final List<String> defaultGoals;
	private final Mvn mvn;

	protected MvnPlugin(Mvn mvn, List<String> defaultGoals) {
		this.mvn = mvn;
		this.defaultGoals = defaultGoals;
	}

	@Override
	public void exec(GetMe.ExecutionContext context, Optional<MvnParams> params) throws GetMe.PluginException {
		boolean hasGoals = params.isPresent() && params.get().hasGoals();
		List<String> goals = hasGoals ? params.get().goals : defaultGoals;
		mvn.run(goals)
				.enableOutput(context.isDebug())
				.in(context.getCwd());
	}

	@Override
	public String toString() {
		return "MvnBuilder";
	}

	@Override
	public Optional<Class<MvnParams>> paramsClass() {
		return Optional.of(MvnParams.class);
	}
}
