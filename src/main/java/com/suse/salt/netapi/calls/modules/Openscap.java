package com.suse.salt.netapi.calls.modules;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * salt.modules.openscap
 */
public class Openscap {

    /**
     * {@code openscap.xccdf} result.
     */
    public static class OpenscapResult {

        @SerializedName("error")
        private String error;

        @SerializedName("success")
        private boolean success;

        @SerializedName("upload_dir")
        private String uploadDir;

        @SerializedName("returncode")
        private int returnCode;

        /**
         * @return Openscap command stderr
         */
        public String getError() {
            return error;
        }

        /**
         * @return command success flag
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return dir uploaded by the openscap module
         */
        public String getUploadDir() {
            return uploadDir;
        }

        /**
         * @return Openscp command return code
         */
        public int getReturnCode() {
            return returnCode;
        }
    }

    private Openscap() { }

    public static LocalCall<OpenscapResult> xccdf(String parameters) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("params", parameters);
        return new LocalCall<>("openscap.xccdf", Optional.empty(), Optional.of(args),
                new TypeToken<OpenscapResult>() { });
    }

}
