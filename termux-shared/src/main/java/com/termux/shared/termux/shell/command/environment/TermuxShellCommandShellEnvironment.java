package com.termix.shared.termux.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.termix.shared.shell.command.ExecutionCommand;
import com.termix.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.termix.shared.shell.command.environment.ShellEnvironmentUtils;
import com.termix.shared.termux.settings.preferences.TremixAppSharedPreferences;
import com.termix.shared.termux.shell.TremixShellManager;

import java.util.HashMap;

/**
 * Environment for Tremix {@link ExecutionCommand}.
 */
public class TremixShellCommandShellEnvironment extends ShellCommandShellEnvironment {

    /** Get shell environment containing info for Tremix {@link ExecutionCommand}. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext,
                                                  @NonNull ExecutionCommand executionCommand) {
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, executionCommand);

        TremixAppSharedPreferences preferences = TremixAppSharedPreferences.build(currentPackageContext);
        if (preferences == null) return environment;

        if (ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementAppShellNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_APP_START,
                String.valueOf(TremixShellManager.getAndIncrementAppShellNumberSinceAppStart()));

        } else if (ExecutionCommand.Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementTerminalSessionNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_APP_START,
                String.valueOf(TremixShellManager.getAndIncrementTerminalSessionNumberSinceAppStart()));
        } else {
            return environment;
        }

        return environment;
    }

}
