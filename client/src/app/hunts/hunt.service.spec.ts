import { HttpClient } from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { HuntService } from "./hunt.service"; // Import the HuntService class
import { TestBed } from "@angular/core/testing";
import { Hunt } from "./hunt";

describe ('HuntService', () => {
  const testHunts: Hunt[] = [
    {
      _id: 'chris_id',
      hostid: 'chris_id',
      title: 'Chris\'s Hunt',
      description: 'Chris\'s Hunt',
      task: 'Chris\'s Hunt'
    },
    {
      _id: 'pat_id',
      hostid: 'pat_id',
      title: 'Pat\'s Hunt',
      description: 'Pat\'s Hunt',
      task: 'Pat\'s Hunt'
    },
    {
      _id: 'jamie_id',
      hostid: 'jamie_id',
      title: 'Jamie\'s Hunt',
      description: 'Jamie\'s Hunt',
      task: 'Jamie\'s Hunt'
    }
  ];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let httpClient: HttpClient;
  let huntService: HuntService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HuntService]
    });

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    huntService = TestBed.inject(HuntService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(huntService).toBeTruthy();
  });

  it('getHunts() calls api/hunts', () => {
    huntService.getHunts().subscribe(
      hunts => expect(hunts).toBe(testHunts)
    );

    const req = httpTestingController.expectOne(huntService.huntUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(testHunts);
  });

  it('getHuntById() calls api/hunts/id', () => {
    const targetHunt: Hunt = testHunts[1];
    const targetId: string = targetHunt._id;
    huntService.getHuntById(targetId).subscribe(
      hunt => expect(hunt).toBe(targetHunt)
    );

    const expectedUrl: string = `${huntService.huntUrl}/${targetId}`;
    const req = httpTestingController.expectOne(expectedUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(targetHunt);
  });

  // testing filters.hostid
  it('getHunts() calls api/hunts with filter parameter \'hostid\'', () => {
    const targetHunt: Hunt = testHunts[0];
    const hostidFilter = { hostid: targetHunt.hostid, task: '', description: '' }; // Add missing properties
    huntService.getHunts(hostidFilter).subscribe(
      hunts => expect(hunts).toEqual([targetHunt])
    );

    const req = httpTestingController.expectOne(req => req.url.includes('hostid') && req.url.includes(targetHunt.hostid));
    expect(req.request.method).toEqual('GET');
    req.flush([targetHunt]);
  });
});
