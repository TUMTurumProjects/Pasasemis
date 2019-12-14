import java.security.{AccessController, Permission}

class CustomSecurityManager() extends SecurityManager {

  /**
   * Custom security checker
   */

  private var isSecure = true;

  override def checkPackageAccess(pkg: String): Unit = {
    None
  }

  override def checkExec(cmd: String): Unit = {
    /**
     * all exec operations are forbidden
     */
    isSecure = false
    throw new SecurityException("Unauthorized system command execution!")
  }

  override def checkConnect(host: String, port: Int): Unit = {
    /**
     * all outside connections are forbidden
     */
    isSecure = false
    throw new SecurityException("Unauthorized connection attempt!")
  }

  override def checkWrite(file: String): Unit = {
    /**
     * all write operations except for the writing the resulting file are forbidden
     */
    if (!file.equals(Settings.JSON_RESULT_NAME)) {
      isSecure = false;
      throw new SecurityException("Unauthorized file write operation!")
    }
  }

  override def checkRead(file: String): Unit = {
    /**
     * All read operations outside of the temporary folder are forbidden
     */
    if (file.indexOf("PGDP-Solution-Checker/judge/tmp") != -1) {
      super.checkRead(file)
    }
  }

  /*
  * deprecated method
  * works but error-prone because of call stack usage
   */
  //  override def checkPermission(perm: Permission): Unit = {
  //
  //    try {
  //      val callStack: Array[StackTraceElement] = Thread.currentThread().getStackTrace()
  //      for (pointer <- 1 until callStack.length) {
  //        if (callStack(0).getMethodName.equals(callStack(pointer).getMethodName)) {
  //          return
  //        }
  //      }
  //    if(!(Settings.WHITE_LIST.contains(perm)))
  //      super.checkPermission(perm)
  //    } catch {
  //      //case e : NoClassDefFoundError => None
  //
  //    }
  //  }

  override def checkPermission(perm: Permission): Unit = {
    /**
     * Checking if the permission is white listed, if not check it against the default set of permissions
     * If it's a reflective operation, check that the caller is not from the package "testee" or "pgdp"
     */
    try {
      if (!Settings.WHITE_LIST.contains(perm) && !Settings.WHITE_LIST_CLASSES.contains(perm.getClass)) {
        AccessController.checkPermission(perm)
      }
      else {
        if (perm.isInstanceOf[RuntimePermission]) {
          if (perm.implies(new RuntimePermission("accessDeclaredMembers"))) {
            val currentClass: String = this.getClassContext()(0) + ""
            if (currentClass.indexOf(Settings.TESTING_PACKAGE_NAME) != -1 || currentClass.indexOf(Settings.PGDP_PACKAGE_NAME) != -1) {
              throw new SecurityException("Illegal reflective operation")
            }
          }
        }
      }
    } catch {
      case e: SecurityException => {
        isSecure = false;
        throw e
      }
      case unknownException: Exception => None
    }
  }

  def securityAlertThrown(): Boolean = {
    !isSecure
  }


}
