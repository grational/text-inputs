package it.grational.input

import spock.lang.*

class FirstAvailableInputUSpec extends Specification {

	@Unroll
	def "Should return the first available among the inputs passed"() {
		given:
			def first  = Mock(TextInput) {
				available() >> firstAvailable
			}
		and:
			def second = Mock(TextInput) {
				available() >> secondAvailable
			}
		when:
			def output = new FirstAvailableInput (
				first,
				second
			).text
		then:
			expectedFirst  * first.getText() >> 'first text'
			expectedSecond * second.getText() >> 'second text'
		and:
			output == expectedOutput
		where:
			firstAvailable | secondAvailable || expectedFirst | expectedSecond | expectedOutput
			true           | false           || 1             | 0              | 'first text'
			false          | true            || 0             | 1              | 'second text'
			true           | true            || 1             | 0              | 'first text'
	}

	def "Should raise an exception if no input is available"() {
		given:
			def first = Stub(TextInput) {
				available() >> false
			}
		and:
			def second = Stub(TextInput) {
				available() >> false
			}
		when:
			new FirstAvailableInput (
				first,
				second
			).text
		then:
			def exception = thrown(IllegalStateException)
			exception.message == "[FirstAvailableInput] No input available!"
	}

}
