from src.data.doctor import Doctor
from src.extractors.info_extractor_interface import IInfoExtractor


class DefaultDoctorExtractor(IInfoExtractor):
    @classmethod
    def extract_info(cls, medical_reports):
        return [[Doctor('Dr. Who')] for i in xrange(len(medical_reports))]