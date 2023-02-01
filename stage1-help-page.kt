package svcs

fun main(args: Array<String>) {
    val help = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file."
    )
    val printHelp = """
        These are SVCS commands:
        config    ${help["config"]}
        add       ${help["add"]}
        log       ${help["log"]}
        commit    ${help["commit"]}
        checkout  ${help["checkout"]}
    """.trimIndent()

    if (args.isEmpty()) {
        print(printHelp)
    } else {
        when (args[0]) {
            "config" -> println("${help["config"]}")
            "add" -> println("${help["add"]}")
            "log" -> println("${help["log"]}")
            "commit" -> println("${help["commit"]}")
            "checkout" -> println("${help["checkout"]}")
            "--help" -> println(printHelp)
            else -> println("'${args[0]}' is not a SVCS command.")
        }
    }


}