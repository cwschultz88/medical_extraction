from src.data.patient import Patient
from src.extractors.info_extractor_interface import IInfoExtractor


class DefaultPatientExtractor(IInfoExtractor):
    '''
    Just assigns the patient name patient_# given a list of medical reports
    '''
    @classmethod
    def extract_info(cls, medical_reports):
        return [Patient('patient_' + str(i)) for i in xrange(len(medical_reports))]