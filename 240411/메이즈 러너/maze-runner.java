import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/**
 * 시작: 21:00
 * 종료: 22:32
 * 
 * 
 * 문제해석
 * NxN크기 미로, (1,1)~
 * 1. 빈칸-이동O
 * 2. 벽-이동X
 * 		1~9 내구도
 * 		회전시 내구도-1
 * 		내구도==0이면 빈칸됨
 * 3. 출구-즉시 탈출
 * 
 * 1초마다 모든 참가자는 1칸씩 이동
 * 최단거리 |x1-x2|+|y1-y2|
 * 모든 참가자는 동시에 이동
 * 상하좌우 4방, 벽이 없는 빈칸만 이동가능
 * 움직인 칸은 현재 칸보다 출구까지의 최단 거리가 가까워야함
 * 		움직일 수 있는 칸이 2개이상->상하 우선순위
 * 움직일 수 없는 상황이면 이동x
 * 한칸에 2명이상의 참가자 가능
 * 	
 * 이동 종료시 미로회전
 * 	1명 이상의 참가자와 출구를 포함한 가장 작은 정사각형
 * 	가장 작은 크기를 갖는 정사각형이 2개이상->좌상단r좌표 작은순->c좌표 작은순
 * 	시계방향 90도 회전->회전된 벽의 내구도-1
 * 
 * k번 반복
 * 	k초 경과전 모든 참가자가 탈출시 게임 종료
 * 게임 종료시 모든 참가자들의 이동 거리 합과 출구 좌표를 출력
 * 
 * 입력
 * 첫째줄: N격자크기,M참가자수,K반복횟수
 * N개줄: 미로정보
 * 	0빈칸,1~9벽 내구도
 * M개줄: 참여자 좌표 r,c
 * 
 * 출력
 * 모든 참가자들의 이동 거리 합과 출구 좌표를 출력
 * 
 * 문제해결프로세스
 * 1. move()
 * 	참가자 동시에 이동
 * 	4방중 출구와 가까워지는 방향 선택
 * 	origin보다 짧아지는 방향
 * 		격자밖x, 상하 우선, 움직이는 방향없으면 이동x
 * 	이동가능 cnt++;
 * 	이동불가
 * 2. square()
 * 	가장 작은 정사각형 잡기
 * 		참가자들 돌면서 한변의 길이 구하기
 * 		2-1. r1-r2 > c1-c2 이면
 * 			len == r1-r2
 *			좌상단 r == min(r1,r2)
 *			좌상단 c == max(max(c1,c2)-len,0)
 *		2-2. r1-r2 <= c1-c2 이면
 *			len == c1-c2
 *			좌상단 r == max(max(r1-r2)-len,0)
 *			좌상단 c == min(c1,c2) 
 * 3. rotate()
 * 	시계방향 90도
 * 	if(ex && ey) 
 * 	copymap[l+r-j][c+i] = min(map[r+i][c+j]-1,0);
 * 	if(ex==r+i && ey==c+j)
 * 		ex=l+r-j
 * 		ey=c+i 
 *  참가자 이동
 *  x가 r~r+l
 *  y가 c~c+l
 *  이 안에 있으면
 *  i=x-r, j=y-c
 *  새위치: (l+r-j,c+i)
 *  	
 * 시간복잡도
 * 
 * 
 */
public class Main {
	static int N,M,K,cnt,ex,ey;
	static int map[][];
	static List<Player> list = new ArrayList<>();
	static PriorityQueue<Square> pq;
	static class Player {
		int x,y;

		public Player(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Player [x=" + x + ", y=" + y + "]";
		}
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			list.add(new Player(x,y));
		}
		st = new StringTokenizer(br.readLine());
		ex = Integer.parseInt(st.nextToken())-1;
		ey = Integer.parseInt(st.nextToken())-1;
		
		for (int i = 0; i < K; i++) {
			move();
			if(list.size()==0) {
				break;
			}
			square();
			rotate();
		}
		System.out.println(cnt);
		System.out.println((ex+1)+" "+(ey+1));
	}
	
	/* 문제해결프로세스
	 * 1. move()
	 * 	참가자 동시에 이동
	 * 	4방중 출구와 가까워지는 방향 선택
	 * 	origin보다 짧아지는 방향
	 * 		격자밖x, 상하 우선, 움직이는 방향없으면 이동x
	 * 	이동가능 cnt++;
	 * 	이동불가
	 * 2. square()
	 * 	가장 작은 정사각형 잡기
	 * 		참가자들 돌면서 한변의 길이 구하기
	 * 		2-1. r1-r2 > c1-c2 이면
	 * 			len == r1-r2
	 *			좌상단 r == min(r1,r2)
	 *			좌상단 c == max(max(c1,c2)-len,0)
	 *		2-2. r1-r2 <= c1-c2 이면
	 *			len == c1-c2
	 *			좌상단 r == max(max(r1-r2)-len,0)
	 *			좌상단 c == min(c1,c2) 
	 * 3. rotate()
	 * 	시계방향 90도
	 * 	if(ex && ey) 
	 * 	copymap[l+r-j][c+i] = max(map[r+i][c+j]-1,0);
	 * 	if(ex==r+i && ey==c+j)
	 * 		ex=l+r-j
	 * 		ey=c+i 
	 *  참가자 이동
	 *  x가 r~r+l
	 *  y가 c~c+l
	 *  이 안에 있으면
	 *  i=x-r, j=y-c
	 *  새위치: (l+r-j,c+i)
	 */
	
	public static void rotate() {
		int copymap[][] = new int[N][N];
		for (int i = 0; i < N; i++) {
			copymap[i] = map[i].clone();
		}
		Square s = pq.poll();
		int r = s.r;
		int c = s.c;
		int len = s.length;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				copymap[r+i][c+j] = Math.max(map[len-1+r-j][c+i]-1, 0);
			}
		}
		int ei = ey-c;
		int ej = len-1+r-ex;
		ex = r+ei;
		ey = c+ej;
		
		//참가자들 이동
		for (int k = 0; k < list.size(); k++) {
			Player p = list.get(k);
			int x = p.x;
			int y = p.y;
			if(x>=r && x<r+len && y>=c && y<c+len) {
				int i=y-c;
				int j=len-1+r-x;
				list.set(k, new Player(r+i,c+j));
			}
		}
		for (int i = 0; i < N; i++) {
			map[i] = copymap[i].clone();
		}
	}
	static class Square implements Comparable<Square>{
		int r,c,length;

		public Square(int r, int c, int length) {
			super();
			this.r = r;
			this.c = c;
			this.length = length;
		}

		@Override
		public int compareTo(Square o) {
			if(this.length==o.length) {
				if(this.r==o.r) {
					return this.c-o.c;
				}
				return this.r-o.r;
			}
			return this.length-o.length;
		}
		
	}
	public static void square() {
		pq = new PriorityQueue<>();
		for (int i = 0; i < list.size(); i++) {
			Player p = list.get(i);
			int x = p.x;
			int y = p.y;
			int len = 0;
			int r = 0;
			int c = 0;
			if(Math.abs(ex-x) > Math.abs(ey-y)) {
				len = Math.abs(ex-x)+1;
				r = Math.min(ex,x);
				c = Math.max(Math.max(ey, y)-len, 0);
			}
			else {
				len = Math.abs(ey-y)+1;
				r = Math.max(Math.max(ex, x)-len, 0);
				c = Math.min(ey,y);
			}
			pq.add(new Square(r,c,len));
		}
	}
	//상하좌우 순
	static int dx[] = {-1,1,0,0};
	static int dy[] = {0,0,-1,1};
	public static void move() {
		int size = list.size();
		for (int i = size-1; i >=0; i--) {
			Player p = list.get(i);
			int x = p.x;
			int y = p.y;
			int origin = Math.abs(x-ex)+Math.abs(y-ey);
			int min = Integer.MAX_VALUE;
			int idx = -1;
			for (int k = 0; k < 4; k++) {
				int nx = x+dx[k];
				int ny = y+dy[k];
				if(!rangecheck(nx,ny) || map[nx][ny]>0) continue;
				//else
				int current = Math.abs(nx-ex)+Math.abs(ny-ey);
				if(current<origin && current<min) {
					min = current;
					idx=k;
				}
			}
			if(min!=Integer.MAX_VALUE) {
				int nx = x+dx[idx];
				int ny = y+dy[idx];
				//탈출 성공
				if(ex==nx && ey==ny) {
					list.remove(i);
				}
				else {
					list.set(i, new Player(nx, ny));
				}
				cnt++;
			}
		}
	}
	
	public static boolean rangecheck(int rx, int ry) {
		return rx>=0 && rx<N && ry>=0 && ry<N;
	}
}