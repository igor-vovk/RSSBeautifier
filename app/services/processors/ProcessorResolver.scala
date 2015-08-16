package services.processors

import com.google.inject.Inject
import play.api.inject.Injector
import repositories.ProcessorConfig


class ProcessorResolver @Inject()(injector: Injector) {

  def apply(config: ProcessorConfig): AsyncProcessor = {
    config.`type` match {
      case "readability" =>
        injector.instanceOf[ReadabilityProcessor]
    }
  }

}
