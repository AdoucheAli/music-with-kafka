package fr.ippon.streamer.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import fr.ippon.streamer.domains.ChartPayload
import fr.ippon.streamer.domains.SoundsPayload
import fr.ippon.streamer.domains.UserMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverRecord

@Service
class Consumer(
        @Qualifier("soundsReceiver") private val soundsReceiver: Flux<ReceiverRecord<String, String>>,
        @Qualifier("chartsReceiver") private val chartsReceiver: Flux<ReceiverRecord<String, String>>,
        @Qualifier("usersReceiver") private val usersReceiver: Flux<ReceiverRecord<String, String>>,
        @Qualifier("userMessageReceiver") private val userMessageReceiver: Flux<ReceiverRecord<String, String>>,
        private val mapper: ObjectMapper) {

    val stream: Flux<SoundsPayload> by lazy {
        soundsReceiver
                .doOnNext { it.receiverOffset().acknowledge() }
                .map {
                    tryOr(SoundsPayload(listOf())) {
                        mapper.readValue(it.value(), SoundsPayload::class.java)
                    }
                }
                .share()
    }

    val chartsStream: Flux<ChartPayload> by lazy {
        chartsReceiver
                .doOnNext { it.receiverOffset().acknowledge() }
                .map {
                    tryOr(ChartPayload(listOf())) {
                        mapper.readValue(it.value(), ChartPayload::class.java)
                    }
                }
                .share()
    }

    val usersStream: Flux<String> by lazy {
        usersReceiver
                .doOnNext { it.receiverOffset().acknowledge() }
                .doOnNext { println("user = ${it.value()}") }
                .map {
                    tryOr("") {
                        it.value()
                    }
                }
                .share()
    }

    val userMessageStream: Flux<UserMessage> by lazy {
        userMessageReceiver
                .doOnNext { it.receiverOffset().acknowledge() }
                .map {
                    tryOr(UserMessage("", "")) {
                        mapper.readValue(it.value(), UserMessage::class.java)
                    }
                }
                .share()
    }

    private fun <T> tryOr(defaultValue: T, f: () -> T): T {
        return try {
            f()
        } catch (e: Exception) {
            defaultValue
        }
    }


}