package com.suse.salt.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Basic operations on files and directories on minions
 */
public class File {

    /**
     * File module result object
     */
    public static class Result {
        private boolean result;
        private String comment;

        public boolean getResult() {
            return result;
        }

        public String getComment() {
            return comment;
        }
    }

    private File() { }

    /**
     * Chown a file
     *
     * @param path  Path to the file or directory
     * @param user  User owner
     * @param group Group owner
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> chown(String path, String user, String group) {
        Map<String, String> args = new LinkedHashMap<>();
        args.put("path", path);
        args.put("user", user);
        args.put("group", group);
        return new LocalCall<>("file.chown", Optional.empty(),
                Optional.of(args), new TypeToken<String>(){});
    }

    /**
     * Set the mode of a file
     *
     * @param path  File or directory of which to set the mode
     * @param mode  Mode to set the path to
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> chmod(String path, String mode) {
        return new LocalCall<>("file.set_mode", Optional.of(Arrays.asList(path, mode)),
                Optional.empty(), new TypeToken<String>(){});
    }

    /**
     * Copy a file or directory from src to dst
     *
     * @param src               File or directory to copy
     * @param dst               Destination path
     * @param recurse           Recurse flag
     * @param removeExisting    If true, all files in the target directory are removed,
     *                          and then the files are copied from the source
     * @return                  The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> copy(String src, String dst, boolean recurse,
            boolean removeExisting) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("src", src);
        args.put("dst", dst);
        args.put("recurse", recurse);
        args.put("remove_existing", removeExisting);
        return new LocalCall<>("file.copy", Optional.empty(), Optional.of(args),
                new TypeToken<Boolean>(){});
    }

    /**
     * Move a file or directory from src to dst
     *
     * @param src   File or directory to copy
     * @param dst   Destination path
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Result> move(String src, String dst) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("src", src);
        args.put("dst", dst);
        return new LocalCall<>("file.move", Optional.empty(), Optional.of(args),
                new TypeToken<Result>(){});
    }

    /**
     * Remove a file
     *
     * @param path  File path to remove
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> remove(String path) {
        return new LocalCall<>("file.remove", Optional.of(Collections.singletonList(path)),
                Optional.empty(), new TypeToken<Boolean>(){});
    }

    /**
     * Get the hash sum of a file
     * <p>
     * SHA256 algorithm is used by default
     *
     * @param path      Path to the file or directory
     * @return          The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> getHash(String path) {
        return getHash(path, Optional.empty(), Optional.empty());
    }

    /**
     * Get the hash sum of a file
     *
     * @param path      Path to the file or directory
     * @param form      Desired sum format
     * @return          The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> getHash(String path, HashType form) {
        return getHash(path, Optional.of(form), Optional.empty());
    }

    /**
     * Get the hash sum of a file
     *
     * @param path      Path to the file or directory
     * @param form      Desired sum format
     * @param chunkSize Amount to sum at once
     * @return          The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> getHash(String path, HashType form, long chunkSize) {
        return getHash(path, Optional.of(form), Optional.of(chunkSize));
    }

    private static LocalCall<String> getHash(String path, Optional<HashType> form,
            Optional<Long> chunkSize) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        if (form.isPresent()) {
            args.put("form", form.get().getHashType());
        }
        if (chunkSize.isPresent()) {
            args.put("chunk_size", chunkSize.get());
        }

        return new LocalCall<>("file.get_hash", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }

    /**
     * Tests to see if path is a valid directory
     *
     * @param path  Path to directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> directoryExists(String path) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        return new LocalCall<>("file.directory_exists", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Tests to see if path is a valid file
     *
     * @param path  Path to file
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> fileExists(String path) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        return new LocalCall<>("file.file_exists", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return the mode of a file
     *
     * @param path              File or directory of which to get the mode
     * @param followSymlinks    Indicated if symlinks should be followed
     * @return                  The {@link LocalCall}
     *                          object to make the call
     */
    public static LocalCall<String> getMode(String path, boolean followSymlinks) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        args.put("follow_symlinks", followSymlinks);
        return new LocalCall<>("file.get_mode", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }

    /**
     * Return the id of the user that owns a given file
     *
     * @param path              File or directory of which to get the uid owner
     * @param followSymlinks    Indicated if symlinks should be followed
     * @return                  The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> getUid(String path, boolean followSymlinks) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        args.put("follow_symlinks", followSymlinks);
        return new LocalCall<>("file.get_uid", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }

    /**
     * Return the user that owns a given file
     *
     * @param path              File or directory of which to get the user owner
     * @param followSymlinks    Indicated if symlinks should be followed
     * @return                  The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> getUser(String path, boolean followSymlinks) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("path", path);
        args.put("follow_symlinks", followSymlinks);
        return new LocalCall<>("file.get_user", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }

    /**
     * Ensures that a directory is available
     *
     * @param path  Path to directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> mkdir(String path) {
        return mkdir(path, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Ensures that a directory is available
     *
     * @param path  Path to directory
     * @param mode  Mode for the newly created directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> mkdir(String path, String mode) {
        return mkdir(path, Optional.empty(), Optional.empty(), Optional.of(mode));
    }

    /**
     * Ensures that a directory is available
     *
     * @param path  Path to directory
     * @param user  Owner user
     * @param group Owner group
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> mkdir(String path, String user, String group) {
        return mkdir(path, Optional.of(user), Optional.of(group), Optional.empty());
    }

    /**
     * Ensures that a directory is available
     *
     * @param path  Path to directory
     * @param user  Owner user
     * @param group Owner group
     * @param mode  Mode for the newly created directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<String> mkdir(String path, String user, String group,
            String mode) {
        return mkdir(path, Optional.of(user), Optional.of(group), Optional.of(mode));
    }

    private static LocalCall<String> mkdir(String path, Optional<String> user,
            Optional<String> group, Optional<String> mode) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("dir_path", path);
        if (user.isPresent()) {
            args.put("user", user.get());
        }
        if (group.isPresent()) {
            args.put("group", group.get());
        }
        if (mode.isPresent()) {
            args.put("mode", mode.get());
        }
        return new LocalCall<>("file.mkdir", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }

    /**
     * Returns a list containing the contents of a directory
     *
     * @param path  Path to directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<List<String>> readdir(String path) {
        return new LocalCall<>("file.readdir", Optional.of(Collections.singletonList(path)),
                Optional.empty(), new TypeToken<List<String>>(){});
    }

    /**
     * Removes the specified directory
     * <p>
     * Fails if the directory is not empty
     *
     * @param path  Path to directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> rmdir(String path) {
        return new LocalCall<>("file.rmdir", Optional.of(Collections.singletonList(path)),
                Optional.empty(), new TypeToken<Boolean>(){});
    }

    /**
     * Check if the path is a symbolic link
     *
     * @param path  Path to file or directory
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> isLink(String path) {
        return new LocalCall<>("file.is_link", Optional.of(Collections.singletonList(path)),
                Optional.empty(), new TypeToken<Boolean>(){});
    }

    /**
     * Create a symbolic link (symlink, soft link) to a file
     *
     * @param src   Path to file or directory
     * @param path  Path to symbolic link
     * @return      The {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> symlink(String src, String path) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("src", src);
        args.put("path", path);
        return new LocalCall<>("file.symlink", Optional.empty(), Optional.of(args)
                , new TypeToken<Boolean>(){});
    }
}
