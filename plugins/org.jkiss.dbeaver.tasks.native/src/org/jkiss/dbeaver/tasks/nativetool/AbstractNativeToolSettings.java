
import org.apache.commons.exec.CommandLine;

public void addExtraCommandArgs(CommandLine cmd) {
    if (!CommonUtils.isEmptyTrimmed(extraCommandArgs)) {
        String[] argsArray = extraCommandArgs.split(" ");
        for (String arg : argsArray) {
            cmd.addArgument(arg, false);
        }
    }
}
