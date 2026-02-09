# Todo

- Refactor the SiteApi 'getSiteById'. The mapping to the response formate makes no sense. 
- the SiteApiIntegrationTest needs some refactoring. 
- Refactor the get planning. Right now the SiteRepository is calling the database function, but I want it in a planning repository
- The planning api errors need to be better. There needs to be a message saying what went wrong. 
- Refactor the planning PATCH endpoint. A patch typically receives a body to update. 
- Check all features and see if cna be simplyfied
- Make 'get idle workers' tests so that order doesn't matter
- Investigate if multiple flush operations in tests are necessary.
- The patch for linking and unlinking makes no sense. Also the url's are no good. one uses worker, the other uses link. 
- Check for idempotency of endpoints