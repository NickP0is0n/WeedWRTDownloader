import java.io.File
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
    packageIntoArchive()
    removeTemporaryFiles()

    println("\nWeedWRT package is created. You can find it in \"output\" folder.")
    println("To launch script on target machine you need to unbox it and run \"weedwrt.sh\" file.")
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


