import java.io.FilePermission
import java.lang.reflect.ReflectPermission
import java.security.Permission
import java.util.PropertyPermission
import java.util.logging.LoggingPermission

/**
 * Settings document to avoid using magic numbers/strings
 */
object Settings {
  val JSON_CONFIG_NAME: String = "config.json"
  val JSON_RESULT_NAME: String = "result.json"
  val WHITE_LIST: Set[Permission] = Set(
    new RuntimePermission("setIO"),
    new PropertyPermission("style.debug", "read"),
    new LoggingPermission("control", null),
    new RuntimePermission("closeClassLoader"),
    new RuntimePermission("modifyThread"),
    new RuntimePermission("getClassLoader"),
    new RuntimePermission("setContextClassLoader"),
    new RuntimePermission("accessDeclaredMembers"),
    new ReflectPermission("suppressAccessChecks"),
    new RuntimePermission("createClassLoader"),
    new RuntimePermission("exitVM.1"),
    new RuntimePermission("exitVM.100"),
    new RuntimePermission("exitVM.0"),
    new RuntimePermission("reflectionFactoryAccess"),
    new RuntimePermission("getProtectionDomain")
  )
  val WHITE_LIST_CLASSES: Set[Class[_]] = Set(
    classOf[FilePermission],
    classOf[java.util.PropertyPermission]
  )

  val TESTING_FILE_NAME_LENGTH: Int = 8;
  val TESTING_PACKAGE_NAME: String = "testee"
  val PGDP_PACKAGE_NAME: String = "pgdp"

}
