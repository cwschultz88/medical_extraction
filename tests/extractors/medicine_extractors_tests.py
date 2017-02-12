import unittest

from src.data.retrieve_medical_report import retrieve_medical_report_localfile
from src.extractors.medicine_extractors import MedexExtractor


class MedexExtractorTests(unittest.TestCase):
    test_report_names = ['tests/test_files/test_report_0.txt',
                         'tests/test_files/test_report_1.txt',
                         'tests/test_files/test_report_2.txt']

    def test_extraction(self):
        '''
        Test get_medical_report_localfile with text medical report file
        '''
        reports = [retrieve_medical_report_localfile(test_report_name) for test_report_name in self.test_report_names]

        extracted_medicines = MedexExtractor.extract_info(reports)

        # make sure have a list of medicine for each report
        self.assertEqual(len(extracted_medicines), len(reports))


if __name__ == '__main__':
    unittest.main()