The job runs multiple mappers in sequence to preprocess the data, and after running reducer, 
it will run multiple mappers in sequence to postprocess the data.Mappers before reduce phase 
can be called preprocessing of data and Mappers after reduce phase can be called postprocessing
of data.

This job Consists of following classes:

    ChainWordCountDriver 
    TokenizerMapper
    UpperCaserMapper
    WordCountReducer
    LastMapper

ChainWordCountDriver will take input file which should be seperated on token. It will call
different mappers and reducers in following sequence

TokenizerMapper -- > UpperCaserMapper -- > WordCountReducer -->  LastMapper
Here the output of one phase will become the input of next phase.


Input data is like this:
customerId,customerName,contactNumber
1,Stephanie Leung,555-555-5555
2,Edward Kim,123-456-7890
3,Jose Madriz,281-330-8004
4,David Stork,408-555-0000

Here the order of execution will be :
1.Driver will call the mappers and reducers in the following sequence.
2. Record will be read in TokenizerMapper, it will parse and split the record on each token[space] and sent it to UpperCaserMapper
3. UpperCaserMapper will do the uppercase of record and send it to  WordCountReducer
4. WordCountReducer will just write the key
5. LastMapper will again split the key written by reducer on comma and write this.


Final Output would be:

1 STEPHANIE
2 EDWARD
3 JOSE
4 DAVID
KIM 123-456-7890
LEUNG 555-555-5555
MADRIZ 281-330-8004
STORK 408-555-0000

It reads first row from file and split the line on token. It tokenize the "Stephanie Leung" and pass (1,Stephanie) To UppercaseMapper
and then pass (Leung,555-555-5555) to UpperCaseMapper and LastMapper will just write these values

Output from different phases:
Line:1,Stephanie Leung,555-555-5555
TokenizerMapper - 1,Stephanie --- 1
UpperCaserMapper output - 1,STEPHANIE --- 1
TokenizerMapper - Leung,555-555-5555 --- 1
UpperCaserMapper output - LEUNG,555-555-5555 --- 1
WordCountReducer output - 1,STEPHANIE --- 0
LastMapper output - 1 --- STEPHANIE
WordCountReducer output - LEUNG,555-555-5555 --- 0
LastMapper output - LEUNG --- 555-555-5555

Line:2,Edward Kim,123-456-7890
TokenizerMapper - 2,Edward --- 1
UpperCaserMapper output - 2,EDWARD --- 1
TokenizerMapper - Kim,123-456-7890 --- 1
UpperCaserMapper output - KIM,123-456-7890 --- 1
WordCountReducer output - 2,EDWARD --- 0
LastMapper output - 2 --- EDWARD
WordCountReducer output - KIM,123-456-7890 --- 0
LastMapper output - KIM --- 123-456-7890

same for different set of rows