import datetime
import os
import unittest

from src.data.input_data_extractors import RetrieveMedicalReportExtractor, CannotHandleFileType


class RetrieveMedicalReportExtractorTests(unittest.TestCase):
    def test_get_file_extractor_textfile(self):
        '''
        Test RetrieveMedicalReportExtractor.get_file_extractor factory method with text file
        '''
        report_extractor = RetrieveMedicalReportExtractor.get_file_extractor('tests/test_files/test.txt')
        report = report_extractor.get_medical_report_data()

        # test 1 - test to makes sure got contents
        self.assertEqual(report.report_contents, 'this is a test')

        # test 2 - make sure got correct date on report
        file_date = datetime.datetime.fromtimestamp(os.path.getmtime('tests/test_files/test.txt'))
        self.assertEqual(file_date, report.report_date)

    def test_get_file_extractor_notsupported(self):
        '''
        Test RetrieveMedicalReportExtractor.get_file_extractor factory method on a file with an extension
        it does not support
        '''
        try:
            report_extractor = RetrieveMedicalReportExtractor.get_file_extractor('tests/test_files/test.fake')
            self.assertEqual(0, 1)
        except CannotHandleFileType, e:
            self.assertEqual(1, 1)


if __name__ == '__main__':
    unittest.main()
