package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.results.GitResult;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyMap;

/**
 * salt.modules.git
 */
public class Git {

    private Git() { }

    public static LocalCall<GitResult> status(String cwd, Optional<String> user) {
        List<String> args = Arrays.asList(cwd);
        user.ifPresent(usr -> args.add(usr));
        return new LocalCall<>("git.status", Optional.of(args),
                Optional.of(emptyMap()), new TypeToken<GitResult>(){});
    }
    
    public static LocalCall<String> add(String cwd, String filename,
            String opts, String gitOpts, Optional<String> user) {
        List<String> args = Arrays.asList(cwd, filename, opts, gitOpts);
        user.ifPresent(usr -> args.add(usr));
        return new LocalCall<>("git.add", Optional.of(args),
                Optional.of(emptyMap()), new TypeToken<String>(){});
    }
    
    public static LocalCall<String> commit(String cwd, String message,
            String opts, String gitOpts, Optional<String> user, Optional<String> filename) {
        List<String> args = Arrays.asList(cwd, message, opts, gitOpts);
        user.ifPresent(usr -> args.add(usr));
        filename.ifPresent(file -> args.add(file));
        return new LocalCall<>("git.commit", Optional.of(args),
                Optional.of(emptyMap()), new TypeToken<String>(){});
    }
    
    public static LocalCall<Boolean> branch(String cwd, String branch,
            String opts, String gitOpts, Optional<String> user) {
        List<String> args = Arrays.asList(cwd, branch, opts, gitOpts);
        user.ifPresent(usr -> args.add(usr));
        return new LocalCall<>("git.branch", Optional.of(args),
                Optional.of(emptyMap()), new TypeToken<Boolean>(){});
    }
}
