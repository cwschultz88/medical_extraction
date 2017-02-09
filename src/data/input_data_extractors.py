class RetrieveMedicalReportExtractor(object):
    '''
    Factory Class that instantiates the appriopriate class implementation of IInputDataFileExtractor

    '''
    @classmethod
    def get_file_extractor(cls, filename):
        pass


class IInputDataFileExtractor(object):
    '''
    Defines interface for medical data input extractors
    '''
    def get_extracted_information(self):
        pass


class _TextFileExtractor(IInputDataFileExtractor):
    '''
    Implementation of IInputDataFileExtractor for medical reports in text files
    '''
    def __init__(self, associated_filename):
        self.associated_filename = associated_filename

    def get_extracted_information(self):
        '''

        '''
        pass