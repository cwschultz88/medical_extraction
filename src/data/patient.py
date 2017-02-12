class Patient(object):
    def __init__(self, name, age=-1, gender='unknown'):
        self.name = name
        self.age = -1
        self.gender = gender

    def __repr__(self):
        return self.name