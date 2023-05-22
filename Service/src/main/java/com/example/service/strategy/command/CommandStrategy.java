package com.example.service.strategy.command;

import com.example.service.strategy.command.model.CommandExecutionRequest;

public interface CommandStrategy {

    public void systemStrategy(CommandExecutionRequest commandExecutionRequest);
}
