import kotlin.math.PI

open class GenericContainer<SELF : GenericContainer<SELF>> {

    @Suppress("UNCHECKED_CAST")
    private fun self() = this as SELF

    open fun name(): SELF = self()
    open fun start(): SELF = self()
}

class Container() : GenericContainer<Container>()

class SpecificContainer : GenericContainer<SpecificContainer>() {

    fun configure() = this
}

fun main() {
    val container = SpecificContainer()
        .name()
        .configure()
        .start()

    Container()
        .name()
        .start()
}
