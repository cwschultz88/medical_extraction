import unittest

from src.extractors.patient_extractors import DefaultPatientExtractor


class DefaultPatientExtractorTests(unittest.TestCase):
    def test_extraction(self):
        '''
        Tests generated patient names from DefaultPatientExtractor
        '''
        reports = [i for i in xrange(5)]
        extracted_patients = DefaultPatientExtractor.extract_info(reports)

        # make sure have a patient for each report
        self.assertEqual(len(extracted_patients), 5)

        # test generated names
        for i in xrange(5):
            self.assertEqual(extracted_patients[i].name, 'patient_' + str(i))


if __name__ == '__main__':
    unittest.main()