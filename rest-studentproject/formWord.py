import inflect

class FormWord:
    def __init__(self, word):
        self.word = word
        self.form = getForm(word)

    def getWord(self):
        return self.word

    def getForm(self):
        return self.form

    def __str__(self):
        return self.word + " (" + self.form + ")"

    def getForm(word):
        p = inflect.engine()
        if p.plural(word) == word:
            return "plural"
        else:
            return "singular"