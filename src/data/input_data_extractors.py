import datetime
import os
import sys

from src.data.medical_report_data import MedicalReportData


class CannotHandleFileType(Exception):
    '''
    Should be raised by RetrieveMedicalReportExtractor Factory
    when request to extract medical report file currently not supported
    '''
    pass


class RetrieveMedicalReportExtractor(object):
    '''
    Factory Class that instantiates the appriopriate class implementation of IInputDataFileExtractor

    '''
    @classmethod
    def get_file_extractor(cls, filename):
        '''
        Factory Method
        '''
        name, file_extension = os.path.splitext(filename)
        if file_extension == '.txt':
            return _TextFileExtractor(filename)
        else:
            raise CannotHandleFileType('Cannot handle ' + file_extension + ' files yet')


class _IInputDataFileExtractor(object):
    '''
    Defines interface for medical data input extractors

    Implementations should return a MedicalReportData object with extracted information
    '''
    def get_medical_report_data(self):
        pass


class _TextFileExtractor(_IInputDataFileExtractor):
    '''
    Implementation of __IInputDataFileExtractor for medical reports in text files
    '''
    def __init__(self, associated_filename):
        self.associated_filename = associated_filename

    def get_medical_report_data(self):
        '''
        '''
        with open(self.associated_filename, 'rb') as f:
            return MedicalReportData(f.read(), datetime.datetime.fromtimestamp(os.path.getmtime(self.associated_filename)))
