package it.grational.input

/**
 * Returns an iterator on the Moqu csv report 
 * Data refers to the campains which ends in the last 7 days
 * @author grational
 * @date 09-03-2021 06.50
 */
class UrlInput implements TextInput {

	private final URL url
	private final Map connectionParameters

	/**
	 * Secondary Constructor
	 * @param URL the URL to connect to
	 */
	UrlInput(URL url) {
		this(url,[:],[:])
	}

	/**
	 * Secondary Constructor
	 * @param URL the URL to connect to
	 * @param cp the connection parameters as specified here:
	 * http://docs.groovy-lang.org/latest/html/groovy-jdk/java/net/URL.html#getText(java.util.Map)
	 * http://mrhaki.blogspot.it/2011/09/groovy-goodness-use-connection.html
	 */
	UrlInput (
		URL url,
		Map cp
	) {
		this(url,cp,[:])
	}
	/**
	 * Primary Constructor
	 * <p>
	 * @param url the URL to connect to
	 * @param cp the connection parameters as specified here:
	 * http://docs.groovy-lang.org/latest/html/groovy-jdk/java/net/URL.html#getText(java.util.Map)
	 * http://mrhaki.blogspot.it/2011/09/groovy-goodness-use-connection.html
	 * @param rp the request properties (custom headers added to the request)
	 */
	UrlInput (
		URL url,
		Map cp,
		Map rp
	) {
		this.url = url
		this.connectionParameters = cp
		this.connectionParameters << [
			requestProperties: rp
		]
	}

	@Override
	Boolean available() {
		def result
		try {
			def code = this.url.openConnection().with {
				requestMethod = 'HEAD'
				this.connectionParameters.requestProperties.each { k, v ->
					addRequestProperty(k,v)
				}
				connect()
				responseCode
			}
			result = (code == 200)
		} catch (e) {
			result = false
		}
		return result
	}

	@Override
	String getText() {
		this.url.getText(this.connectionParameters)
	}
}
