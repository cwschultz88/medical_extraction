from src.data.medical_history import MedicalHistory


class Patient(object):
    def __init__(self, name, age=-1, gender='unknown', previous_medical_history=None):
        self.name = name
        self.age = -1
        self.gender = gender
        if previous_medical_history:
            self.medical_history = previous_medical_history
        else:
            self.medical_history = MedicalHistory()

    def __repr__(self):
        return self.name