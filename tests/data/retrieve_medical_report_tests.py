import datetime
import os
import unittest

from src.data.retrieve_medical_report import retrieve_medical_report_localfile, CannotHandleFileType


class GetMedicalReportTests(unittest.TestCase):
    def test_get_medical_report_localfile_text(self):
        '''
        Test get_medical_report_localfile with text medical report file
        '''
        report = retrieve_medical_report_localfile('tests/test_files/test.txt')

        # test 1 - test to makes sure got contents
        self.assertEqual(report.report_contents, 'this is a test')

        # test 2 - make sure got correct date on report
        report_date = datetime.datetime.fromtimestamp(os.path.getmtime('tests/test_files/test.txt'))
        self.assertEqual(report_date, report.report_date)

    def test_get_medical_report_localfile_notsupported(self):
        '''
        Test get_medical_report_localfile on a file with an extension it does not support
        '''
        try:
            retrieve_medical_report_localfile('tests/test_files/test.fake')
            self.assertEqual(0, 1)
        except CannotHandleFileType, e:
            self.assertEqual(1, 1)


if __name__ == '__main__':
    unittest.main()
