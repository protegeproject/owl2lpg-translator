package edu.stanford.owl2lpg.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "owl2lpg",
    subcommands = {
        Owl2LpgTranslateCommand.class
    }
)
public class Owl2LpgCommand implements Callable<Integer> {

  @Spec
  Model.CommandSpec spec;

  public static void main(String... args) {
    var cmd = new CommandLine(new Owl2LpgCommand());
    if (args.length == 0) {
      cmd.usage(System.out);
    } else {
      cmd.execute(args);
    }
  }

  @Override
  public Integer call() throws Exception {
    throw new ParameterException(spec.commandLine(), "Specify a subcommand");
  }
}
