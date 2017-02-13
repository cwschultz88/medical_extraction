import datetime
import os
import sys

from src.data.medical_report import MedicalReport


def retrieve_medical_report_localfile(report_filename):
    __, file_extension = os.path.splitext(report_filename)
    if file_extension == '.txt':
        report_retriever = _TextReportRetriever(report_filename)
    else:
        raise CannotHandleFileType('Cannot handle ' + file_extension + ' files yet')

    return report_retriever.get_medical_report()


def retrieve_medical_reports_localfiles(report_filenames):
    return [retrieve_medical_report_localfile(filename) for filename in report_filenames]


class CannotHandleFileType(Exception):
    '''
    Should be raised by RetrieveMedicalReportExtractor Factory
    when request to extract medical report file currently not supported
    '''
    pass


class _IMedicalReportRetriever(object):
    '''
    Defines interface for medical data input extractors

    Implementations should return a MedicalReportData object with extracted information
    '''
    def get_medical_report(self):
        pass


class _TextReportRetriever(_IMedicalReportRetriever):
    '''
    Implementation of __IInputDataFileExtractor for medical reports in text files
    '''
    def __init__(self, associated_filename):
        self.associated_filename = associated_filename

    def get_medical_report(self):
        '''
        '''
        with open(self.associated_filename, 'rb') as f:
            return MedicalReport(f.read(), datetime.datetime.fromtimestamp(os.path.getmtime(self.associated_filename)))
