package com.tremix.app.terminal;

import android.app.Service;

import androidx.annotation.NonNull;

import com.tremix.app.TremixService;
import com.tremix.shared.termux.shell.command.runner.terminal.TremixSession;
import com.tremix.shared.termux.terminal.TremixTerminalSessionClientBase;
import com.tremix.terminal.TerminalSession;
import com.tremix.terminal.TerminalSessionClient;

/** The {@link TerminalSessionClient} implementation that may require a {@link Service} for its interface methods. */
public class TremixTerminalSessionServiceClient extends TremixTerminalSessionClientBase {

    private static final String LOG_TAG = "TremixTerminalSessionServiceClient";

    private final TremixService mService;

    public TremixTerminalSessionServiceClient(TremixService service) {
        this.mService = service;
    }

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        TremixSession termuxSession = mService.getTremixSessionForTerminalSession(terminalSession);
        if (termuxSession != null)
            termuxSession.getExecutionCommand().mPid = pid;
    }

}
