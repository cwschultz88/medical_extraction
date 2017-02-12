from src.data.medical_history import MedicalHistory


class Patient(object):
    def __init__(self, name, age=-1, gender='unknown', previous_medical_history = MedicalHistory()):
        self.name = name
        self.age = -1
        self.gender = gender
        self.medical_history = previous_medical_history

    def __repr__(self):
        return self.name