UPDATE `erp_works`
SET 
  `zone` = LPAD(`zone`, 2, '0'),
  `ward` = LPAD(`ward`, 3, '0');

INSERT INTO `erp_works_task` (`estid`, `estimate_no`, `sub_cat_id`, `question_id`, `ans_id`, `remarks`) 
	 SELECT DISTINCT 
	     estid, 
	     estimate_no,
	     sub_category, 
	     1 AS question_id, 
	     project_name AS ans_id, 
	     NULL AS remarks
	 FROM `erp_works`  ew
	 WHERE  NOT EXISTS (
	        SELECT 1 
	        FROM erp_works_task ews 
	        WHERE ews.estid = ew.estid
	    );

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
SELECT 
    ew.estid,
    twtm.code,
    twtm.taskname,
    twtm.orderby,
    twtm.remarks
FROM (
    SELECT DISTINCT estid 
    FROM `erp_works` ew
    WHERE (project_name LIKE "%BM%" 
           OR project_name LIKE "%BC%" 
           OR project_name LIKE "%BT%"
		   OR project_name LIKE "%B.T%")
      AND sub_category = 4 
      AND category IN (8, 9, 14, 15)
	  AND NOT EXISTS (
	        SELECT 1 
	        FROM erp_works_stages ews 
	        WHERE ews.estid = ew.estid
	    )
) ew
CROSS JOIN (
    SELECT * 
    FROM typeofwork_task_master 
    WHERE code = '4RBT'
) twtm;

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
SELECT 
    ew.estid,
    twtm.code,
    twtm.taskname,
    twtm.orderby,
    twtm.remarks
FROM (
    SELECT DISTINCT estid 
    FROM `erp_works` ew
    WHERE (project_name LIKE "%BM%" 
           OR project_name LIKE "%BC%" 
           OR project_name LIKE "%BT%"
		   OR project_name LIKE "%B.T%")
      AND sub_category = 4 
      AND category IN (1,4,7,11)
	  AND NOT EXISTS (
	        SELECT 1 
	        FROM erp_works_stages ews 
	        WHERE ews.estid = ew.estid
	    )
) ew
CROSS JOIN (
    SELECT * 
    FROM typeofwork_task_master 
    WHERE code = '4FBT'
) twtm;

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
SELECT 
    ew.estid,
    twtm.code,
    twtm.taskname,
    twtm.orderby,
    twtm.remarks
FROM (
    SELECT DISTINCT estid 
    FROM `erp_works` ew
    WHERE (
	project_name LIKE "%CC%" OR 
	project_name LIKE "%concrete%" OR 
	project_name LIKE "%Interlocking%" OR 
	project_name LIKE "%Inter locking%" OR 
	project_name LIKE "%ILP%" OR
	project_name LIKE "%ILB%" OR
	project_name LIKE "%inlet%Chamber%" )
      AND sub_category = 4 
      AND category IN (8, 9, 14, 15)
	  AND NOT EXISTS (
	        SELECT 1 
	        FROM erp_works_stages ews 
	        WHERE ews.estid = ew.estid
	    )
) ew
CROSS JOIN (
    SELECT * 
    FROM typeofwork_task_master 
    WHERE code = '4RCC'
) twtm;


INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
SELECT 
    ew.estid,
    twtm.code,
    twtm.taskname,
    twtm.orderby,
    twtm.remarks
FROM (
    SELECT DISTINCT estid 
    FROM `erp_works` ew
    WHERE (
	project_name LIKE "%CC%" OR 
	project_name LIKE "%concrete%" OR 
	project_name LIKE "%Interlocking%" OR 
	project_name LIKE "%Inter locking%" OR 
	project_name LIKE "%ILP%" OR
	project_name LIKE "%ILB%" OR
	project_name LIKE "%inlet%Chamber%" )
      AND sub_category = 4 
      AND category IN (1,4,7,11)
	  AND NOT EXISTS (
	        SELECT 1 
	        FROM erp_works_stages ews 
	        WHERE ews.estid = ew.estid
	    )
) ew
CROSS JOIN (
    SELECT * 
    FROM typeofwork_task_master 
    WHERE code = '4FCC'
) twtm;

 INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
   SELECT 
       ew.estid,
       twtm.code,
       twtm.taskname,
       twtm.orderby,
       twtm.remarks
   FROM (
       SELECT DISTINCT estid 
       FROM `erp_works` ew 
       WHERE sub_category = 4 
	   	AND category IN (7,8,9,14,15)
   	  	 AND NOT EXISTS (
   	        SELECT 1 
   	        FROM erp_works_stages ews 
   	        WHERE ews.estid = ew.estid
   	    )
   ) ew
   CROSS JOIN (
       SELECT * 
       FROM typeofwork_task_master 
       WHERE code = '0SS'
   ) twtm;

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
	   SELECT 
	       ew.estid,
	       twtm.code,
	       twtm.taskname,
	       twtm.orderby,
	       twtm.remarks
	   FROM (
	       SELECT DISTINCT estid 
	       FROM `erp_works` ew
	       WHERE sub_category <> 4 
	         AND category IN (2,3,5,6,8,9,10,11,12,13,14,15)
			 AND NOT EXISTS (
			 	        SELECT 1 
			 	        FROM erp_works_stages ews 
			 	        WHERE ews.estid = ew.estid
			 	    )
	   ) ew
	   CROSS JOIN (
	       SELECT * 
	       FROM typeofwork_task_master 
	       WHERE code = '0SS'
	   ) twtm;

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
	   SELECT 
	       ew.estid,
	       twtm.code,
	       twtm.taskname,
	       twtm.orderby,
	       twtm.remarks
	   FROM (
	       SELECT DISTINCT estid 
	       FROM `erp_works` ew
	       WHERE sub_category =15 
	         AND category IN (8,10,15)
			 AND NOT EXISTS (
			 	        SELECT 1 
			 	        FROM erp_works_stages ews 
			 	        WHERE ews.estid = ew.estid
			 	    )
	   ) ew
	   CROSS JOIN (
	       SELECT * 
	       FROM typeofwork_task_master 
	       WHERE code = '0SS'
	   ) twtm;	

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
   SELECT 
       ew.estid,
       twtm.code,
       twtm.taskname,
       twtm.orderby,
       twtm.remarks
   FROM (
       SELECT DISTINCT estid 
       FROM `erp_works` ew
       WHERE sub_category = 15 
         AND category IN (7)
		 AND NOT EXISTS (
		 	        SELECT 1 
		 	        FROM erp_works_stages ews 
		 	        WHERE ews.estid = ew.estid
		 	    )
   ) ew
   CROSS JOIN (
       SELECT * 
       FROM typeofwork_task_master 
       WHERE code = '15NB'
   ) twtm; 
   

INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`)
 SELECT 
     ew.estid,
     twtm.code,
     twtm.taskname,
     twtm.orderby,
     twtm.remarks
 FROM (
     SELECT DISTINCT estid 
     FROM `erp_works` ew
     WHERE sub_category = 7 
       AND category IN (7)
	 AND NOT EXISTS (
	 	        SELECT 1 
	 	        FROM erp_works_stages ews 
	 	        WHERE ews.estid = ew.estid
	 	    )
 ) ew
 CROSS JOIN (
     SELECT * 
     FROM typeofwork_task_master 
     WHERE code = '7SWD'
 ) twtm;	 