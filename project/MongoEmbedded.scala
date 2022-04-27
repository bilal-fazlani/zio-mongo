
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.mongo.config.{ MongodConfig, Net}
import java.util.concurrent.atomic.AtomicInteger
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
// import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.config.Defaults
import de.flapdoodle.embed.process.config.process
import de.flapdoodle.embed.mongo.packageresolver.Command

object MongoEmbedded {

  lazy val defaultPort = 27017
  lazy val host = "localhost"

  val runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD)
    .processOutput(process.ProcessOutput.silent())
    .build();

  lazy val mongodConfig = {
    MongodConfig
      .builder()
      .version(Version.Main.DEVELOPMENT)
      .net(new Net(host, defaultPort, Network.localhostIsIPv6))
      .build
  }

  private val counter = new AtomicInteger(0)

  @volatile var mongodExe: MongodExecutable = null
  @volatile var mongod: MongodProcess = null

  def start: Unit = synchronized {
    if (mongod == null) {
      mongodExe = MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig)
      mongod = mongodExe.start()
    }
    counter.incrementAndGet()
  }

  def stop: Unit = synchronized {
    if (counter.decrementAndGet() == 0) {
      shutdownMongo()
    }
  }

  def shutdownMongo(): Unit = {
    try {
      mongod.stop()
      mongod = null
    } finally {
      mongodExe.stop()
      mongodExe = null
    }
  }

  sys.addShutdownHook { () =>
    mongod.stop()
    mongodExe.stop()
  }
}


