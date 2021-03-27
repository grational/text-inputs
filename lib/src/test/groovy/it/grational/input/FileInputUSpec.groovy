package it.grational.input

import spock.lang.*

class FileInputUSpec extends Specification {
	
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
		File.createTempFile("temp",".tmp").tap {
			deleteOnExit()
			write "temporary content"
		}
	}

	private File nonExistingFile() {
		def nonExistingFile = this.temporaryFile()
		nonExistingFile.delete()
		return nonExistingFile
	}

}
