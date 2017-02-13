import unittest

from src.extractors.doctor_extractors import DefaultDoctorExtractor


class DefaultDoctorExtractorTests(unittest.TestCase):
    def test_extraction(self):
        '''
        Tests generated doctors from DefaultDoctorExtractor
        '''
        reports = [i for i in xrange(5)]
        extracted_doctors = DefaultDoctorExtractor.extract_info(reports)

        # make sure have a doctor for each report
        self.assertEqual(len(extracted_doctors), 5)

        # test generated names
        for i in xrange(5):
            self.assertEqual(extracted_doctors[i][0].name, 'Dr. Who')


if __name__ == '__main__':
    unittest.main()