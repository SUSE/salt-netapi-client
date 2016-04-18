package com.suse.salt.netapi.results;

public class ModuleNotSupported implements SaltError {

   private final String moduleName;

   public ModuleNotSupported(String module) {
      this.moduleName = module;
   }

   public String getModuleName() {
      return moduleName;
   }

   @Override
   public String toString() {
      return "ModuleNotSupported(" + moduleName + ")";
   }
}
