-- funny, useful, cool,user_id, review_id, stars, text, date, type, business_id
-- convert json data into tab separated file (.tsv) file

reviews = load 'data/yelp_academic_dataset_business.json'
        using JsonLoader('votes:map[],user_id:chararray,review_id:chararray,stars:int,date:chararray,text:chararray,type:chararray,business_id:chararray');

tabs = FOREACH reviews generate
  (INT) votes#'funny', (INT) votes#'useful', (INT) votes#'cool', user_id, review_id, stars, REPLACE(REPLACE(text, '\n', ''), '\t', ''), date, type, business_id;

STORE tabs INTO 'yelp_academic_dataset_review.tsv';