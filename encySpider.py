import datetime
from urllib.parse import urljoin
import scrapy

class EncySpider(scrapy.Spider):
    name = 'encyspider'
    allowed_domains = ['www.encyclopedia.com']
    start_urls = ['https://www.encyclopedia.com/women/encyclopedias-almanacs-transcripts-and-maps/glasgow-ellen-1873-1945', 
                  'https://www.encyclopedia.com/books/politics-and-business-magazines/systems-computer-technology-corp',
                  'https://www.encyclopedia.com/history/modern-europe/british-and-irish-history/united-kingdom-great-britain-and-northern-ireland',
                  'https://www.encyclopedia.com/reference/social-sciences-magazines/gambling-united-states-overview',
                  'https://www.encyclopedia.com/science-and-technology/biology-and-genetics/biology-general/kingdom',
                  'https://www.encyclopedia.com/earth-and-environment/atmosphere-and-weather/weather-and-climate-terms-and-concepts/fog',
                  'https://www.encyclopedia.com/history/encyclopedias-almanacs-transcripts-and-maps/wars-and-empires',
                  'https://www.encyclopedia.com/education/dictionaries-thesauruses-pictures-and-press-releases/sugar-doctor',
                  'https://www.encyclopedia.com/psychology/dictionaries-thesauruses-pictures-and-press-releases/vienna-general-hospital',
                  'https://www.encyclopedia.com/reference/encyclopedias-almanacs-transcripts-and-maps/bachelors-button',
                  'https://www.encyclopedia.com/education/technology-degrees/cyber-security',
                  'https://www.encyclopedia.com/science/encyclopedias-almanacs-transcripts-and-maps/internet-explosion',
                  'https://www.encyclopedia.com/humanities/dictionaries-thesauruses-pictures-and-press-releases/moderation-all-things',
                  'https://www.encyclopedia.com/social-sciences-and-law/law/law/information',
                  'https://www.encyclopedia.com/science/encyclopedias-almanacs-transcripts-and-maps/international-fortean-organization-info',
                  'https://www.encyclopedia.com/social-sciences/applied-and-social-sciences-magazines/state-dependent-retrieval',
                  'https://www.encyclopedia.com/retrieve',
                  'https://www.encyclopedia.com/education/encyclopedias-almanacs-transcripts-and-maps/sacramental-universe',
                  'https://www.encyclopedia.com/religion/encyclopedias-almanacs-transcripts-and-maps/coimbra-university'
                  ]
    max_depth = 5  # Set the maximum depth of recursion

    custom_settings = {
        'ROBOTSTXT_OBEY': True,
        'DOWNLOAD_FAIL_ON_DATALOSS': False,  # Continue even if response parsing fails
    }

    def __init__(self, *args, **kwargs):
        super(EncySpider, self).__init__(*args, **kwargs)
        self.url_frontier = set()  # Store unique URLs
        self.url_depth = {}  # Store depth of each URL
        self.processed_content = set()  # Store processed content

        # Read the file and add each URL to the set
        try:
            with open('url_frontier.txt', 'r') as f:
                for line in f:
                    url = line.strip()  # Remove newline character
                    self.url_frontier.add(url)
        except FileNotFoundError:
            self.logger.info("url_frontier.txt not found. Starting with an empty set.")

    def start_requests(self):
        for url in self.start_urls:
            yield scrapy.Request(url, callback=self.parse, meta={'depth': 0}, errback=self.errback_http_failure)

    def parse(self, response):
        current_url = response.url
        current_depth = response.meta.get('depth', 0)

        # Trapguard for infinite looping
        if current_url in self.url_depth and self.url_depth[current_url] <= current_depth:
            return

        # Trapguard for duplicate content
        page_content = response.xpath('//div[@id="topic_wrap"]').get()
        if page_content in self.processed_content:
            return
        self.processed_content.add(page_content)

        # Getting the box that contains all the info we want
        result_container = response.xpath('//div[@id="topic_wrap"]')

        # Looping through each result listed in the result_container box
        for result in result_container:
            result_title = result.xpath('.//h1[@class="doctitle"]/text()').get()
            result_content = result.xpath('.//div[@class="doccontentwrapper collapse show"]/p/text()').getall()
            result_urls = result.xpath('.//a[not(contains(@style, "display:none"))]/@href').getall()  # Ignore hidden links
            result_timestamp = datetime.datetime.now()

            # Return data extracted
            yield {
                'title': result_title,
                'content': result_content,
                'datetime': result_timestamp,
                'url': current_url
            }

            if result_urls and current_depth < self.max_depth:
                for next_page in result_urls:
                    absolute_url = urljoin(response.url, next_page)
                    if absolute_url not in self.url_frontier:  # Check if the URL has already been visited
                        # Update URL frontier and depth
                        self.url_frontier.add(absolute_url)
                        self.url_depth[absolute_url] = current_depth + 1

                        yield response.follow(next_page, self.parse, meta={'depth': current_depth + 1},
                                              errback=self.errback_http_failure)
    def closed(self, reason):
        # Save the URL frontier to a text file when the spider is closed
        with open('url_frontier.txt', 'w') as f:
            for url in self.url_frontier:
                f.write(url + '\n')

    def errback_http_failure(self, failure):
        # Handling specific HTTP status errors here
        request = failure.request
        if failure.value.response.status in [404, 503]:
            self.logger.error(f"Failed to fetch URL: {request.url} (Status: {failure.value.response.status})")
        else:
            self.logger.error(f"Failed to fetch URL: {request.url} (Status: {failure.value.response.status}) - "
                              f"Unknown error")