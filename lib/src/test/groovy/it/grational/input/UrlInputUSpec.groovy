package it.grational.input

import spock.lang.*
// wiremock imports
import com.github.tomakehurst.wiremock.WireMockServer
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

class UrlInputUSpec extends Specification {

	@Shared String  protocol    = 'http'
	@Shared String  defaultHost = 'localhost'
	@Shared Integer defaultPort = 1234
	@Shared String  origin      = "${protocol}://${defaultHost}:${defaultPort}"
	@Shared WireMockServer wms

	def setupSpec() {
		wms = new WireMockServer(options().port(defaultPort))
		wms.start()
		Integer.metaClass.getSeconds { delegate * 1000 }
	}

	def cleanupSpec() {
		wms.stop()
	}

	@Unroll
	def "Should report the correct availability of a url"() {
		given:
			wms.stubFor (
				head(urlPathEqualTo(inputPath))
				.willReturn (
					aResponse()
					.withStatus(expectedStatus)
				)
			)
		when:
			TextInput urlInput = new UrlInput("${origin}${inputPath}".toURL())

		then:
			urlInput.available() == expectedAvailability

		and:
			wms.verify (
				1,
				headRequestedFor (
					urlPathEqualTo(inputPath)
				)
			)

		where:
			inputPath            || expectedStatus | expectedAvailability
			'/existing/path'     || 200            | true
			'/non_existing/path' || 404            | false
	}

	def "Should report an invalid domain as not available"() {
		given: 'a non existing domain'
			def nonExistingUrl = 'https://non.existing-domain.ned'.toURL()
		when:
			TextInput urlInput = new UrlInput(nonExistingUrl)
		then:
			urlInput.available() == false
	}

	def "Should correctly pass the connection parameters"() {
		given:
			def readTimeout = 3.seconds
		and:
			def inputPath = '/existing/path'
		and:
			wms.stubFor (
				get(urlPathEqualTo(inputPath))
				.willReturn (
					aResponse()
					.withStatus(200)
					.withFixedDelay(readTimeout + 2.seconds)
					.withBody("We don't have to receive this")
				)
			)
		and:
			def urlInput = new UrlInput (
				"${origin}${inputPath}".toURL(),
				[ readTimeout: readTimeout ]
			)

		when:
			urlInput.text

		then:
			def exception = thrown(SocketTimeoutException)
			exception.message == "Read timed out"

		and: 'only one request made to the wiremock server'
			wms.verify (
				1,
				getRequestedFor (
					urlPathEqualTo(inputPath)
				)
			)
	}

	@IgnoreRest
	def "Should correctly set custom headers both for availability and text"() {
		given:
			def token = 'this_is_a_secret_token'
		and:
			def inputPath = '/existing/path'
		and:
			wms.stubFor (
				get(urlPathEqualTo(inputPath))
				.withHeader("token", equalTo(token))
				.willReturn (
					aResponse()
					.withStatus(200)
				)
			)
		and:
			wms.stubFor (
				head(urlPathEqualTo(inputPath))
				.withHeader("token", equalTo(token))
				.willReturn (
					aResponse()
					.withStatus(200)
				)
			)
		and:
			def urlInput = new UrlInput (
				"${origin}${inputPath}".toURL(),
				[:],
				[ token: token ]
			)

		when:
			def availability = urlInput.available()

		then:
			availability == true
		and:
			wms.verify (
				1,
				headRequestedFor (
					urlPathEqualTo(inputPath)
				)
				.withHeader("token", equalTo(token))
			)

		when:
			urlInput.text

		then:
			wms.verify (
				1,
				getRequestedFor (
					urlPathEqualTo(inputPath)
				)
				.withHeader("token", equalTo(token))
			)

	}

}
