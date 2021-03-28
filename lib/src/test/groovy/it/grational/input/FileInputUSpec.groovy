package it.grational.input

import spock.lang.*
import it.grational.specification.*

class FileInputUSpec extends Specification {

	@Shared
	TempFileFactory tff = new TempFileFactory()
	
	def "Should be lenient if a null file is passed"() {
		given:
			def nullFile = null
		and:
			def lenientFileInput = new FileInput(nullFile)
		expect:
			lenientFileInput.available() == false
			lenientFileInput.getText() == ''
	}

	@Unroll
	def "Should signal readability of a file through the available method"() {
		when:
			def fileInput = new FileInput(input)

		then:
			fileInput.available() == expected

		where:
			input             || expected
			temporaryFile()   || true
			nonExistingFile() || false
	}

	private File temporaryFile() {
		tff.create('non important content')
	}

	private File nonExistingFile() {
		def nonExistingFile = this.temporaryFile()
		nonExistingFile.delete()
		return nonExistingFile
	}

}
