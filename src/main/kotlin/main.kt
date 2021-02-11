import java.io.File
import java.io.PrintWriter
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

val OWRT_URL = "https://downloads.openwrt.org"
val OWRT_CORE_PKG = listOf("libiwinfo", "libiwinfo-lua")
val OWRT_BASE_PKG = listOf("libjson-c2", "liblua", "lua", "libuci-lua", "libubus", "libubus-lua", "uhttpd", "rpcd")
val OWRT_LUCI_PKG = listOf("luci-base", "liblucihttp", "liblucihttp-lua", "luci-lib-ip", "luci-lib-nixio", "luci-theme-bootstrap", "luci-mod-admin-full", "luci-lib-jsonc", "luci-mod-status", "luci-mod-system", "luci-mod-network")

val TEMP_DIRECTORY = File("tmp/luci-offline")

lateinit var OWRT_DIR: String
lateinit var OWRT_COREURL: String
lateinit var OWRT_BASEURL: String
lateinit var OWRT_LUCIURL: String

fun main(args: Array<String>) {
    if (args.size < 3) {
        showHelpMessage()
    }

    println("WeedWRT Downloader v${BuildInfo.version} (${BuildInfo.branch} branch)")
    println("Created by NickP0is0n (github.com/NickP0is0n)\n")

    println("[*] Setting up download sources...")
    val releaseType = checkReleaseType(args[0])
    val version = args[0]
    setOpenWrtDirectory(version, releaseType)
    setDownloadUrl(platform = args[1], architecture = args[2])

    println("[*] Creating temp directory...")
    TEMP_DIRECTORY.mkdirs()

    downloadCorePackages()
    downloadBasePackages()
    downloadLuciPackages()
    makeInstallScript()
}

fun showHelpMessage() {
    println("Usage: weedwrtloader [SNAPSHOT/release version] [platform] [architecture]")
    exitProcess(0)
}

fun checkReleaseType(type: String): ReleaseType = when {
    type.equals("snapshot", ignoreCase = true) -> ReleaseType.SNAPSHOT
    else -> ReleaseType.RELEASE
}

fun setOpenWrtDirectory(version: String, releaseType: ReleaseType) {
    OWRT_DIR = when (releaseType) {
        ReleaseType.SNAPSHOT -> "snapshots"
        ReleaseType.RELEASE -> "releases/${version}"
    }
}

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
            packageUrl.openStream().use {
                Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
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
            packageUrl.openStream().use {
                Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
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
            packageUrl.openStream().use {
                Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    println("[*] LuCI packages download complete.")
}

fun downloadCorePackageList(): File {
    println("\n[*] Downloading core package list...")
    val packageListUrl = URL("$OWRT_COREURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    packageListUrl.openStream().use {
        Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
    return packageLocalFile
}

fun downloadBasePackageList(): File {
    println("\n[*] Downloading base package list...")
    val packageListUrl = URL("$OWRT_BASEURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    packageListUrl.openStream().use {
        Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
    return packageLocalFile
}

fun downloadLuciPackageList(): File {
    println("\n[*] Downloading LuCI package list...")
    val packageListUrl = URL("$OWRT_LUCIURL/Packages")
    val packageLocalFile = File("$TEMP_DIRECTORY/Package")
    packageListUrl.openStream().use {
        Files.copy(it, packageLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
    return packageLocalFile
}

fun makeInstallScript() {
    println("\n[*] Generating installation script...")
    val script = """
        echo "WeedWRT v${BuildInfo.version} (${BuildInfo.branch} branch)"
        echo "Gentleman kit for OpenWRT snapshots"
        echo "Created by NickP0is0n (github.com/NickP0is0n)"
        echo " "
        read  -n 1 -p "Press any key to start"
        echo "[*] Unboxing LuCI files to temp directory"
        mkdir -p "/tmp/luci-offline-packages"
        cp luci-offline/*.ipk "/tmp/luci-offline-packages"
        echo "[*] Installing LuCI and dependencies"
        cd "/tmp/luci-offline-packages"
        opkg install *.ipk
        echo "[*] Removing temp files"
        rm -rf "/tmp/luci-offline-packages"
        echo "[*] Starting uhttpd service"
        /etc/init.d/uhttpd start
        /etc/init.d/uhttpd enable
        echo "Done"
    """.trimIndent()

    val output = File("$TEMP_DIRECTORY/weedwrt.sh")
    output.createNewFile()

    PrintWriter(output).use {
        it.print(script)
    }
    println("[*] Installation script generated.")
}


