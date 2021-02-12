import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

fun setDownloadUrl(platform: String, architecture: String) {
    OWRT_COREURL = "$OWRT_URL/$OWRT_DIR/targets/$platform/packages"
    OWRT_BASEURL = "$OWRT_URL/$OWRT_DIR/packages/$architecture/base"
    OWRT_LUCIURL = "$OWRT_URL/$OWRT_DIR/packages/$architecture/luci"
}

fun downloadCorePackages() {
    val packageListLocalFile = downloadCorePackageList()

    println("[*] Parsing core package list...")
    val packageParser = PackageParser(packageListLocalFile)

    OWRT_CORE_PKG.forEach { packageName ->
        val currentPackage = packageParser.packageList.find { it.name == packageName }
        if (currentPackage == null) {
            println("[*] Package $packageName not found, skipped.")
        }
        else {
            println("[*] Downloading package ${currentPackage.name} (${currentPackage.filename})...")
            val packageUrl = URL("$OWRT_COREURL/${currentPackage.filename}")
            val packageLocalFile = File("$TEMP_DIRECTORY/${currentPackage.filename}")
            downloadFile(packageUrl, packageLocalFile)
        }
    }

    println("[*] Core packages download complete.")
}

fun downloadBasePackages() {
    val packageListLocalFile = downloadBasePackageList()

    println("[*] Parsing base package list...")
    val packageParser = PackageParser(packageListLocalFile)

    OWRT_BASE_PKG.forEach { packageName ->
        val currentPackage = packageParser.packageList.find { it.name == packageName }
        if (currentPackage == null) {
            println("[*] Package $packageName not found, skipped.")
        }
        else {
            println("[*] Downloading package ${currentPackage.name} (${currentPackage.filename})...")
            val packageUrl = URL("$OWRT_BASEURL/${currentPackage.filename}")
            val packageLocalFile = File("$TEMP_DIRECTORY/${currentPackage.filename}")
            downloadFile(packageUrl, packageLocalFile)
        }
    }

    println("[*] Base packages download complete.")
}

fun downloadLuciPackages() {
    val packageListLocalFile = downloadLuciPackageList()

    println("[*] Parsing LuCI package list...")
    val packageParser = PackageParser(packageListLocalFile)

    OWRT_LUCI_PKG.forEach { packageName ->
        val currentPackage = packageParser.packageList.find { it.name == packageName }
        if (currentPackage == null) {
            println("[*] Package $packageName not found, skipped.")
        }
        else {
            println("[*] Downloading package ${currentPackage.name} (${currentPackage.filename})...")
            val packageUrl = URL("$OWRT_LUCIURL/${currentPackage.filename}")
            val packageLocalFile = File("$TEMP_DIRECTORY/${currentPackage.filename}")
            downloadFile(packageUrl, packageLocalFile)
        }
    }

    println("[*] LuCI packages download complete.")
}

fun downloadCorePackageList(): File {
    println("\n[*] Downloading core package list...")
    val packageListUrl = URL("$OWRT_COREURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    downloadFile(packageListUrl, packageLocalFile)
    return packageLocalFile
}

fun downloadBasePackageList(): File {
    println("\n[*] Downloading base package list...")
    val packageListUrl = URL("$OWRT_BASEURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    downloadFile(packageListUrl, packageLocalFile)
    return packageLocalFile
}

fun downloadLuciPackageList(): File {
    println("\n[*] Downloading LuCI package list...")
    val packageListUrl = URL("$OWRT_LUCIURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    downloadFile(packageListUrl, packageLocalFile)
    return packageLocalFile
}

fun downloadFile(url: URL, target: File) {
    url.openStream().use {
        Files.copy(it, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}